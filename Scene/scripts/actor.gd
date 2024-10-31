extends CharacterBody3D

# The port we will listen to.
const PORT = 9080
var tcp_server := TCPServer.new()
var ws := WebSocketPeer.new()

var move_flag = false
var json = JSON.new()

var old_position = Vector3(0, 0, 0)
var old_rotation = Vector3(0, 0, 0)
var rot = "up"
var seeing = false
var rotating = false
var saw = {}

@onready var nav = $NavigationAgent3D

func log_message(message):
	print("[LOG]\t", Time.get_time_string_from_system() + " " + message)

func err_message(message):
	print("[ERR]\t", Time.get_time_string_from_system() + " " + message)

func _ready():
	if tcp_server.listen(PORT) != OK:
		err_message("Unable to start server.")
		set_process(false)


func _physics_process(delta):
	if $AnimationPlayer.is_playing():
		translate(-Vector3.FORWARD * delta)
		
	while tcp_server.is_connection_available():
		var conn: StreamPeerTCP = tcp_server.take_connection()
		assert(conn != null)
		ws.accept_stream(conn)

	ws.poll()

	if ws.get_ready_state() == WebSocketPeer.STATE_OPEN:
		while ws.get_available_packet_count():
			var msg = ws.get_packet().get_string_from_ascii()
			log_message(msg)
			#var idea = JSON.parse_string(msg)
			#manage(idea)
	
	see()

func _exit_tree():
	ws.close()
	tcp_server.stop()
	
		
func compute_obj_name(obj):
	var obj_str = obj.name.left(-1)
	var room_n = obj.get_parent().name.right(1)
	var obj_n = obj.name.right(1)
	return obj_str + room_n + obj_n

func see():
	# There are some interesting situations in which the actor should not "perceive" objects to not overflow:
	#	- if it didn't move (equals rotation and position)
	#	- if it is moving (otherwise it will send a message at each frame!)
	#	- if it is rotating (there could be errors with misplaced objects in the brain)
	if (rotation == old_rotation and position == old_position) or $AnimationPlayer.is_playing() or rotating:
		return
	
	# A semaphor: when it's true don't do anything else, otherwise the position and rotation
	# could cause problems updating while seeing.
	seeing = true
	# Store the updated values of rotation and position
	old_rotation = rotation
	old_position = position
	
	# Get the sight and lateral sight nodes
	var sight = $Rig/Skeleton3D/Rogue_Head_Hooded/FrontalView
	var lateral_sight = $Rig/Skeleton3D/Rogue_Head_Hooded/LateralView
	
	# Get all the frontal overlapping bodies
	var objects = sight.get_overlapping_bodies()
	objects.erase(self)
	# Get all the lateral overlapping bodies
	var lateral_objects = lateral_sight.get_overlapping_bodies()
	lateral_objects.erase(self)
	# Concatenate lists
	objects.append_array(lateral_objects)
	
	var obj_names = []
	
	if len(objects) > 0:
		var space_state = get_world_3d().direct_space_state
		var sights = []
		for obj in objects:
			# Compute the vector from sight to object and check intersection to check occlusions
			var target_vector = PhysicsRayQueryParameters3D.create(sight.global_position, obj.global_position - sight.global_position)
			if space_state.intersect_ray(target_vector):
				var obj_name = compute_obj_name(obj)
				obj_names.append(obj_name)
				sights.append(obj_name)
		var perception = {'sender': 'body', 'receiver': 'vesna', 'perception': 'sight', 'data': {'sights': sights}}
		log_message(sights)
		log_message(JSON.stringify(perception))
		# Send the message to the mind
		ws.send_text(JSON.stringify(perception))
				
	
	seeing = false

func manage_rotate(idea):
	while(seeing):
		pass
	rotating = true
	var target_position = Vector3(0.0, 0.0, 0.0)
	if (idea["data"]["direction"] == "right"):
		target_position = global_position + Vector3(1.0, 0.0, 0.0)
	elif (idea["data"]["direction"] == "left"):
		target_position = global_position + Vector3(-1.0, 0.0, 0.0)
	elif (idea["data"]["direction"] == "up"):
		target_position = global_position + Vector3(0.0, 0.0, -1.0)
	elif (idea["data"]["direction"] == "down"):
		target_position = global_position + Vector3(0.0, 0.0, 1.0)
	look_at(target_position)
	print("[ROT]\t", rotation)
	await get_tree().create_timer(0.5).timeout
	rotating = false
		
func manage_move(idea):
	while(seeing):
		pass
	var data = idea['data']
	var target = data['target']
	if target == 'random':
		print('I look for a random position')
	else:
		print('I look for ', target)
	#$AnimationPlayer.play("Walking_A")
	
func manage_goto(idea):
	while(seeing):
		pass
	
func manage_requests(idea):
	if idea["data"]["information"] == "position":
		if (idea["data"]["object"] == "me"):
			send_position(position)
		elif (idea["data"]["object"] == "door"):
			var door = get_tree().get_current_scene().get_node("Room1/Wall_Doorway2").position
			send_position(door)
			
	
func send_position(pos):
	var perception = {"perception": "position", "data": {"x": pos[0], "y": pos[2]}}
	print("sending position ", pos)
	ws.send_text(JSON.stringify(perception))

func manage(idea):
	if (idea["action"] == "rotate"):
		manage_rotate(idea)
	if (idea["action"] == "move"):
		manage_move(idea)
	if (idea["action"] == "goto"):
		pass
	if (idea["action"] == "request"):
		manage_requests(idea)
	#ws.send_text("{}")
	
