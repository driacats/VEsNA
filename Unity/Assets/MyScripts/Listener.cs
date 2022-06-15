using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using System;

public class Listener : MonoBehaviour{

    /*** GLOBAL VARIABLES ***/
    // Prefabs objects

    public GameObject[] objects;
    public List<int> counters;
    IDictionary<string, int> idxOf = new Dictionary<string, int>();

    // Global variables to control the execution flow
    public bool finished = false;
    public bool listenerLock = true;
    public bool additionOk = false;
    public bool remotionOk = false;
    public Queue actions = new Queue();
    public bool listening = false;
    public Vector3 sceneSize;

    // The IP address in the LAN
    public string myIP;

    // Start is called before the first frame update
    void Start(){
        // Set the IP address of localhost in lan
        string hostName = Dns.GetHostName();
        myIP = Dns.GetHostByName(hostName).AddressList[0].ToString();
        Debug.Log("Listening on " + myIP);
        // Unity is not listening and has not finished
        listening = false;
        finished = false;
        for(int i=0; i<objects.Length; i++){
            idxOf.Add(objects[i].name, i);
            counters.Add(0);
        }
        sceneSize = GetComponent<Collider>().bounds.size;
    }

    // Update is called once per frame
    void Update(){
        // if Unity is not listening but has not finished starts the listener
        if (!listening && !finished){
            Task.Run(() => { SimpleListener("http://" + myIP + ":8081/"); });
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
                Tuple<String, String, String> action = (Tuple<String, String, String>) actions.Dequeue();
                if (action.Item1 != "remove"){ 
                    Vector3 position = getPosition(action.Item2, action.Item3); // get the vector3 position
                    // Check if the object is placed onto another. This is useful for place availability check.
                    bool on = (action.Item2 == "on");
                    // If the place is available decides which function to call depending on the object
                    if (checkAvailability(position, on)){
                        GameObject obj = Instantiate(objects[idxOf[action.Item1]], position, Quaternion.identity) as GameObject;
                        obj.transform.localScale = new Vector3(2, 2, 2);
                        obj.name = action.Item1 + counters[idxOf[action.Item1]]++.ToString();
                        additionOk = true; // If the place is available set the value to true
                        Debug.Log("Object added");
                    }else{
                        additionOk = false; // If the place is not available set the value to false
                        Debug.Log("Object NOT added");
                    }
                }
                else{
                    Debug.Log("Removing...");
                    GameObject objRemove = GameObject.Find(action.Item2.Replace("%20", " "));
                    if (objRemove != null){
                        Destroy(objRemove);
                        remotionOk = true;
                    }
                    else
                        remotionOk = false;
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
        Vector3 position = new Vector3(0, 10, 0);
        // The default position vector is (0, 0, 0).
        // We do not update it in case of center.
        if (posX == "right")
            position += new Vector3(0, 0, sceneSize[2] / 2 - sceneSize[2] * 0.2f);
        else if (posX == "left")
            position += new Vector3(0, 0, -sceneSize[2] / 2 - sceneSize[2] * 0.2f);
        else if (posX == "leftOf" || posX == "rightOf" || posX == "behindOf" || posX == "frontOf" || posX == "on"){
            Debug.Log("Referenced Object: " + posY);
            GameObject reference = GameObject.Find(posY.Replace("%20", " ")); // Get the referenced object
            Vector3 refPosition = reference.transform.position; // Get the position of the referenced object
            Vector3 refSize = reference.GetComponent<Collider>().bounds.size; // Get the size of the object
            if (posX == "leftOf"){
                position = refPosition += new Vector3(0, 10, -refSize[2] - 3.0f);
                Debug.Log("Left position: " + position.ToString());
            }
            else if (posX == "rightOf")
                position = refPosition += new Vector3(0, 0, refSize[2] + 3.0f);
            else if (posX == "behindOf")
                position = refPosition += new Vector3(refSize[0] + 3.0f, 0, 0);
            else if (posX == "frontOf")
                position = refPosition += new Vector3(-refSize[0] - 3.0f, 0, 0);
            else if (posX == "on"){
                position = refPosition += new Vector3(0, refSize[1] / 2, 0);
            }
        }
        if (posY == "front")
            position += new Vector3(sceneSize[0] / 2 - sceneSize[0] * 0.2f, 0, 0);
        else if (posY == "behind")
            position += new Vector3(-sceneSize[0] / 2 - sceneSize[0] * 0.2f, 0, 0);
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

        String url = request.RawUrl; // Get the request: /object/posX/posY
        // We split the request and we will have: [ , object, posX, posY] (Notice that the first position is empty since the string starts with a /)
        String[] infos = url.Split('/');
        // Initialize variables
        String obj = infos[1].Replace("%20", " ");
        String posX = null;
        String posY = null;
        string responseString = "<HTML><BODY>DONE!</BODY></HTML>";

        switch (infos.Length){
            case 4: // If the length is 4 all the informations are provided
                // obj  = infos[1];
                posX = infos[2];
                posY = infos[3];
                break;
            case 3: // If the length is 3 we don't have posY
                // obj  = infos[1];
                posX = infos[2];
                posY = "center";
                break;
            case 2: // If the length is 2 we have only the object
                // obj  = infos[1];
                posX = "center";
                posY = "center";
                break;
            default:
                throw new ArgumentException("infos");
        }
        Debug.Log("Got connection!");
        // only a workaround.
        // if (obj == "favicon.ico"){
        //     return;
        // }

        String objName = ""; // The object name to be used as reference in future to add new objects
        if (obj != "exit"){
            // enqueue the correct name depending on obj and set the objName for the response
            actions.Enqueue(new Tuple<String, String, String>(obj, posX, posY));
            objName = obj + counters[idxOf[obj]].ToString();

            while(listenerLock){ // wait until the object is added
                Thread.Sleep(1);
            }
            if (additionOk) // If the object has been correctly added set the done response
                responseString = "<HTML><BODY>DONE !" + objName + "!</BODY></HTML>";
            if (remotionOk){
                Debug.Log("Send response");
                responseString = "<HTML><BODY>DONE</BODY></HTML>";
            }
            if(!additionOk && !remotionOk) // the failure one otherwise
                responseString = "<HTML><BODY>PLACE NOT AVAILABLE!</BODY></HTML>";
        }
        else{
            if (obj == "exit"){ // if the command is to exit set finished to true and set the response
                finished = true;
                responseString = "<HTML><BODY>EXIT!</BODY></HTML>";
            }
        }

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
