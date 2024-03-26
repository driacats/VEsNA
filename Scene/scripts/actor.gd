extends CharacterBody3D

# The port we will listen to.
const PORT = 9080
var tcp_server := TCPServer.new()
var ws := WebSocketPeer.new()

var move_flag = false
var json = JSON.new()

var old_position = position
var old_rotation = rotation


func log_message(message):
	print("[LOG]\t", Time.get_time_string_from_system() + " " + message)

func err_message(message):
	print("[ERR]\t", Time.get_time_string_from_system() + " " + message)

func _ready():
	if tcp_server.listen(PORT) != OK:
		err_message("Unable to start server.")
		set_process(false)


func _process(delta):
	see()
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

func _exit_tree():
	ws.close()
	tcp_server.stop()

func see():
	#if (rotation == old_rotation and position == old_position):
		#return
	#old_rotation = rotation
	#old_position = position
	var sight = $Rig/Skeleton3D/Rogue_Head_Hooded/Area3D
	var objects = sight.get_overlapping_bodies()
	if len(objects) > 0:
		var space_state = get_world_3d().direct_space_state
		for obj in objects:
			var target_vector = PhysicsRayQueryParameters3D.create(sight.global_position, obj.global_position - sight.global_position)
			if space_state.intersect_ray(target_vector):
				#print("[SEE]\t", obj.name)
				var distance = sight.global_position.distance_to(obj.global_position)
				var perception = {"perception": "sight", "data": {"object": obj.name, "distance": str(distance)}}
				ws.send_text(JSON.stringify(perception))

func manage_rotate(idea):
	print("[ROT]\t", rotation)
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
		
func manage_move(idea):
	$AnimationPlayer.play("Walking_A")

func manage(idea):
	if (idea["action"] == "rotate"):
		manage_rotate(idea)
	if (idea["action"] == "move"):
		manage_move(idea)
	#ws.send_text("{}")
	
