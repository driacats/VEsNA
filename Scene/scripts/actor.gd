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
			#log_message(msg)
			var idea = JSON.parse_string(msg)
			manage(idea)
	
	see()

func _exit_tree():
	ws.close()
	tcp_server.stop()
	
func dist_to_logic(distance):
	if (distance < 3.5):
		return "touch";
	elif (distance < 5.0):
		return "near";
	elif (distance < 8.0):
		return "medium";
	else:
		return "far";
		
func vector_to_direction(vector):
	if (vector.y < 4.0):
		return "down";
	if (vector.y < 2.0):
		return "left";
	if (vector.y < 1.0):
		return "up";
	if (vector.y < 0.0):
		return "right";
		
func compute_obj_name(obj):
	var obj_str = obj.name.left(-1)
	var room_n = obj.get_parent().name.right(1)
	var obj_n = obj.name.right(1)
	return obj_str + room_n + obj_n
	
func get_side(my_x, obj_x):
	if (obj_x - my_x) > 0: 
		return "right"
	else:
		return "left" 

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
	if len(objects) > 0:
		var space_state = get_world_3d().direct_space_state
		for obj in objects:
			# Compute the vector from sight to object and check intersection to check occlusions
			var target_vector = PhysicsRayQueryParameters3D.create(sight.global_position, obj.global_position - sight.global_position)
			if space_state.intersect_ray(target_vector):
				var float_distance = sight.global_position.distance_to(obj.global_position)
				var distance = dist_to_logic(float_distance)
				var direction = vector_to_direction(rotation)
				var obj_name = compute_obj_name(obj)
				var perception = {"perception": "sight", "data": {"object": obj_name, "distance": distance, "rotation": direction, "position": position, "side": "front"}}
				print("[SEE] room: ", obj.get_parent().name, ", ", JSON.stringify(perception))
				# Send the message to the mind
				ws.send_text(JSON.stringify(perception))
	else:
		print("[SEE] No objects seen in front.")
				
	var lateral_objects = lateral_sight.get_overlapping_bodies()
	if len(lateral_objects) > 0:
		var space_state = get_world_3d().direct_space_state
		for obj in lateral_objects:
			# Compute the vector from sight to object and check intersection to check occlusions
			var target_vector = PhysicsRayQueryParameters3D.create(lateral_sight.global_position, obj.global_position - lateral_sight.global_position)
			if space_state.intersect_ray(target_vector):
				var float_distance = lateral_sight.global_position.distance_to(obj.global_position)
				var distance = dist_to_logic(float_distance)
				
				var side = get_side(global_position.x, obj.global_position.x)
				var obj_name = compute_obj_name(obj)
				var direction = vector_to_direction(rotation)
				var perception = {"perception": "sight", "data": {"object": obj_name, "distance": distance, "rotation": direction, "position": position, "side": side}}
				print("[SEE] room: ", obj.get_parent().name, ", ", JSON.stringify(perception))
				# Send the message to the mind
				ws.send_text(JSON.stringify(perception))
	else:
		print("[SEE] No objects on the sides.")
			
	
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
	# print("[ROT] ", rotation, ", ", global_rotation)
	look_at(target_position)
	print("[ROT]\t", rotation)
	# var rotate = {"perception": "rotation", "rotation": rotation}
	# ws.send_text(JSON.stringify(rotate))
	await get_tree().create_timer(0.5).timeout
	rotating = false
		
func manage_move(idea):
	while(seeing):
		pass
	print("[POS]\t", position)
	$AnimationPlayer.play("Walking_A")
	
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
	
