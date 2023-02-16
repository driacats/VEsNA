using UnityEngine;

public class Actor : MonoBehaviour{

	public Animator Anim;
	private Vector3 from;
	public Vector3 to;
	private float distance;
	private float duration = 10;
	private bool walk = true;
	
	void Start(){
		from = transform.position;
		distance = Vector3.Distance(from, to);
		Anim.SetBool("Walk", true);
	}

	void Update(){
		if(walk){
			Quaternion targetRotation = Quaternion.LookRotation(to - transform.position);
			targetRotation.x = 0;
			targetRotation.z = 0;
			transform.rotation = Quaternion.Lerp(transform.rotation, targetRotation, 1);
			transform.position = Vector3.MoveTowards(transform.position, to, (distance/duration) * Time.deltaTime);
			if(Vector3.Distance(to, transform.position) < 0.5f){
				Quaternion targetRot = Quaternion.LookRotation(from - to);
				transform.rotation = targetRot;
				Vector3 swap = to;
				to = from;
				from = swap;
			}
		}
	}

}