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
			log_message(msg)
			var idea = JSON.parse_string(msg)
			manage(idea)

func _exit_tree():
	ws.close()
	tcp_server.stop()

func see():
	if (rotation == old_rotation and position == old_position):
		return
	old_rotation = rotation
	old_position = position
	var sight = $Rig/Skeleton3D/Rogue_Head_Hooded/Area3D
	var objects = sight.get_overlapping_bodies()
	for obj in objects:
		print("[SEE]\t", obj.name)
		ws.send_text("I saw " + obj.name)
	#var collision = $RayCast3D.get_collider()
	#print("Current rotation: ", $RayCast3D.global_rotation)
	#if collision:
		#print(collision.name)

func manage_rotate(idea):
	print("[ROT]\t", rotation)
	if (idea["data"]["direction"] == "right"):
		rotate(Vector3.UP, deg_to_rad(-90))
	if (idea["data"]["direction"] == "left"):
		rotate(Vector3.UP, deg_to_rad(90))
		
func manage_move(idea):
	$AnimationPlayer.play("Walking_A")

func manage(idea):
	if (idea["action"] == "rotate"):
		manage_rotate(idea)
	if (idea["action"] == "move"):
		manage_move(idea)
	ws.send_text("Messaggio ricevuto!")
	
