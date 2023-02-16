using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using System;

public class Listener : MonoBehaviour{

    /*** GLOBAL VARIABLES ***/
    public GameObject[] objects;
    public GameObject actor;
    public List<int> counters;
    public int actorCounter = 0;
    public IDictionary<string, int> idxOf = new Dictionary<string, int>();

    public struct Infos{
        public bool relAddition;
        public bool globAddition;
        public bool removal;
        public bool motion;

        public string posX;
        public string posY;
        public string posRel;
        public string objRel;
        public string objName;
        public string direction;
    };

    private void printInfos(Infos infos){
        Debug.Log("relAddition: " + infos.relAddition);
        Debug.Log("globAddition: " + infos.globAddition);
        Debug.Log("removal: " + infos.removal);
        Debug.Log("motion: " + infos.motion);
        Debug.Log("posX: " + infos.posX);
        Debug.Log("posY: " + infos.posY);
        Debug.Log("posRel: " + infos.posRel);
        Debug.Log("objRel: " + infos.objRel);
        Debug.Log("objName: " + infos.objName);
        Debug.Log("direction: " + infos.direction);
    }

    // Global variables to control the execution flow
    private bool finished = false;
    private bool listenerLock = true;
    private bool listening = false;
    // TODO: actionPerformed va sostituito con una queue di booleani che diventano true quando necessario per essere coerente con quanto fatto per passare le actions da eseguire.
    private bool actionPerformed = false;
    private Queue actions = new Queue();
    private Vector3 sceneSize;

    // The IP address in the LAN
    private string myIP;

    // Start is called before the first frame update
    void Start(){
        // Set the IP address of localhost in lan
        string hostName = Dns.GetHostName();
        myIP = Dns.GetHostEntry(hostName).AddressList[0].ToString();
        Debug.Log("Listening on " + myIP);
        // Unity is not listening and has not finished
        listening = false;
        finished = false;
        for(int i=0; i<objects.Length; i++){
            idxOf.Add(objects[i].name, i);
            counters.Add(0);
        }
        sceneSize = GetComponent<Collider>().bounds.size;
        Debug.Log(sceneSize);
    }

    // Update is called once per frame
    void Update(){
        // if Unity is not listening but has not finished starts the listener
        if (!listening && !finished){
            Task.Run(() => { SimpleListener("http://127.0.0.1:8081/"); });
            listening = true;
            // I had to launch the listener on a different task in order to make the interface available
            // This is simple to produce but introduces a new problem: the new task has no access to Unity variables
            // So, the listener simply enqueues the actions to be performed and the principal task do it when they appear.
        }
        // Check if there are objects in queue and adds them (usually only one at a time)
        updateStage();
    }

    void updateStage(){
        if (actions.Count > 0){
            // While there are actions to be performed it is done
            while(actions.Count > 0){
                // Get the object, the position with the two strings posX and posY
                Infos action = (Infos)actions.Dequeue();
                // Debug.Log("Action received:");
                // printInfos(action);

                if (action.relAddition || action.globAddition){
                    Vector3 position;
                    if (action.relAddition)
                        position = getPosition(action.posRel, action.objRel);
                    else
                        position = getPosition(action.posX, action.posY);
                    bool on = (action.posRel == "on");
                    if (checkAvailability(position, on)){
                        GameObject obj = Instantiate(objects[idxOf[action.objName]], position, Quaternion.identity) as GameObject;
                        obj.name = action.objName + counters[idxOf[action.objName]]++.ToString();
                        Debug.Log(position);
                        actionPerformed = true;
                    }
                }
                if (action.removal){
                    GameObject obj = GameObject.Find(action.objRel);
                    if (obj){
                        Destroy(obj);
                        actionPerformed = true;
                    }
                }

                listenerLock = false; // Unlock the listener
            }
        }
    }

    // This function returns the position vector given the strings in input.
    // There are 9 positions available on the stage:
    //
    //      +--------+--------+--------+   ^
    //      | behind | behind | behind |   |
    //      |  left  | center | right  |   |
    //      +--------+--------+--------+   p
    //      | center | center | center |   o
    //      |  left  | center | right  |   s
    //      +--------+--------+--------+   Y
    //      | front  | front  | front  |   |
    //      |  left  | center | right  |   |
    //      +--------+--------+--------+   v
    //
    //      <-----------posX----------->
    //
    // Or you can add objects with reference to others, for example:
    //     "Add a box right of Box0" produces:
    //       
    //      +--------+
    //      |        |  +----| shift from the border by some more space
    //      |  Box0  |  v
    //      |    +---|---> o new object position
    //      +--------+
    //       ^  \___/
    //       |    +---| get the "radius of the object
    //       +-------------------| get position of the object

    Vector3 getPosition(String posX, String posY){
        Vector3 position = new Vector3(0, 2, 0);
        // The default position vector is (0, 0, 0).
        // We do not update it in case of center.
        if (posX == "right")
            position += new Vector3(sceneSize[0] / 2 - sceneSize[0] * 0.2f, 0, 0);
        else if (posX == "left")
            position += new Vector3(-sceneSize[0] / 2 - sceneSize[0] * 0.2f, 0, 0);
        else if (posX == "leftOf" || posX == "rightOf" || posX == "behindOf" || posX == "frontOf" || posX == "on"){
            GameObject reference = GameObject.Find(posY.Replace("%20", " ")); // Get the referenced object
            Vector3 refPosition = reference.transform.position; // Get the position of the referenced object
            Vector3 refSize = reference.GetComponent<Collider>().bounds.size; // Get the size of the object
            if (posX == "leftOf")
                position = refPosition += new Vector3(-refSize[0] - 0.5f, 1, 0);
            else if (posX == "rightOf")
                position = refPosition += new Vector3(refSize[0] + 0.5f, 0, 0);
            else if (posX == "behindOf")
                position = refPosition += new Vector3(0, 0, refSize[2] + 0.5f);
            else if (posX == "frontOf")
                position = refPosition += new Vector3(0, 0, -refSize[2] - 0.5f);
            else if (posX == "on"){
                position = refPosition += new Vector3(0, refSize[1] / 2, 0);
            }
        }
        if (posY == "front")
            position += new Vector3(0, 0, -sceneSize[2] / 2 + sceneSize[2] * 0.2f);
        else if (posY == "behind")
            position += new Vector3(0, 0, sceneSize[2] / 2 - sceneSize[2] * 0.2f);
        return position;
    }

    // This function checks the availability of the position selected.
    // Returns true if the place is available, false otherwise.
    bool checkAvailability(Vector3 position, bool on){
        Collider[] objPresents = Physics.OverlapSphere(position, 0.01f); // creates a new collider in the object position
        if ((objPresents.Length == 0 || // if there is no other object
            objPresents.Length == 1 && position[1] == 0) || // or there is an object but it is the floor
            objPresents.Length == 1 && on){ // or there is one object ant it is because the new object it is onto another one
            return true;
            }
        // in all the other cases
        return false;
    }

    void InsertActor(){
        GameObject obj = Instantiate(actor, new Vector3(0,2,0), Quaternion.identity);
        obj.name = "Actor" + actorCounter.ToString();
    }

    void MoveActor(Vector3 targetPosition){
        
    }

    private bool str2bool(String s){
        return (s.Contains("true"));
    }

    private Infos GetRequestData (HttpListenerRequest request){
        if (!request.HasEntityBody){
            Debug.Log("No client data was sent with the request.");
            // TODO: throw new error
        }
        System.IO.Stream body = request.InputStream;
        System.Text.Encoding encoding = request.ContentEncoding;
        System.IO.StreamReader reader = new System.IO.StreamReader(body, encoding);

        String s = reader.ReadToEnd();
        body.Close();
        reader.Close();
        Infos infos = new Infos();
        String[] rawdata = s.Split(',');

        for(int i=0; i<rawdata.Length; i++){
            // // Debug.Log("[" + i + "] " + rawdata[i]);
            if (rawdata[i].Split(": ")[0].Contains("relAddition"))
                infos.relAddition = str2bool(rawdata[i].Split(": ")[1].Replace("\"", ""));
            if (rawdata[i].Split(": ")[0].Contains("globAddition"))
                infos.globAddition = str2bool(rawdata[i].Split(": ")[1].Replace("\"", ""));
            if (rawdata[i].Split(": ")[0].Contains("removal"))
                infos.removal = str2bool(rawdata[i].Split(": ")[1].Replace("\"", ""));
            if (rawdata[i].Split(": ")[0].Contains("motion"))
                infos.motion = str2bool(rawdata[i].Split(": ")[1].Replace("\"", ""));
            if (rawdata[i].Split(": ")[0].Contains("posX"))
                infos.posX = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
            if (rawdata[i].Split(": ")[0].Contains("posY"))
                infos.posY = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
            if (rawdata[i].Split(": ")[0].Contains("posRel"))
                infos.posRel = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
            if (rawdata[i].Split(": ")[0].Contains("objRel"))
                infos.objRel = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
            if (rawdata[i].Split(": ")[0].Contains("objName"))
                infos.objName = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
            if (rawdata[i].Split(": ")[0].Contains("direction"))
                infos.direction = rawdata[i].Split(": ")[1].Replace("\"", "").Replace(" ", "");
        }
        return infos;
        // If you are finished with the request, it should be closed also.
    }

    // LISTENER CODE
    void SimpleListener(string prefix){
        if (!HttpListener.IsSupported){
            Debug.Log("Windows XP SP2 or Server 2003 is required to use the HttpListnere class.");
            return;
        }
        if (prefix == null || prefix.Length == 0)
            throw new ArgumentException("prefix");

        HttpListener listener = new HttpListener(); // Creates a listener
        listener.Prefixes.Add(prefix); // Gives the listener the address and the gate to listen
        listener.Start(); // Start listening
        // Now when a request is received the lock waits for the Unity to add the object before send response
        listenerLock = true;

        // The listener is now active and will asynchronouslly call the ListenerCallBack function when it receives something
        IAsyncResult result = listener.BeginGetContext(new AsyncCallback(ListenerCallback), listener);
        result.AsyncWaitHandle.WaitOne();
    }

    void ListenerCallback(IAsyncResult result){
        
        HttpListener listener = (HttpListener) result.AsyncState; // Get the listener object and its useful informations
        HttpListenerContext context = listener.EndGetContext(result);
        HttpListenerRequest request = context.Request;

        Infos infos = GetRequestData(request);
        Debug.Log("Got connection!");

        actions.Enqueue(infos);
        
        while(listenerLock)
            Thread.Sleep(1);

        string responseString = "<HTML><BODY>DONE !";
        if (!actionPerformed)
            responseString += " ERROR ";
        else if (actionPerformed && (infos.relAddition || infos.globAddition))
            responseString += infos.objName + counters[idxOf[infos.objName]].ToString();
        else if (actionPerformed && infos.removal)
            responseString += " DONE ";
        else if (actionPerformed && infos.motion)
            responseString += " MOVED ";
        
        responseString += " !</BODY></HTML>";

        HttpListenerResponse response = context.Response;
        response.StatusDescription = "OK";
        response.StatusCode = (int) HttpStatusCode.OK;

        byte[] buffer = System.Text.Encoding.UTF8.GetBytes(responseString);

        response.ContentLength64 = buffer.Length;
        System.IO.Stream output = response.OutputStream;
        output.Write(buffer, 0, buffer.Length); // Send response

        output.Close();
        listener.Stop();
        listening = false; // The task is no more listening
    }  
}
