extends CharacterBody3D

# The port we will listen to.
const PORT = 9080
var tcp_server := TCPServer.new()
var socket := WebSocketPeer.new()

var move_flag = false


func log_message(message):
	print(Time.get_time_string_from_system() + " " + message)


func _ready():
	if tcp_server.listen(PORT) != OK:
		log_message("Unable to start server.")
		set_process(false)


func _process(_delta):
	while tcp_server.is_connection_available():
		var conn: StreamPeerTCP = tcp_server.take_connection()
		assert(conn != null)
		socket.accept_stream(conn)

	socket.poll()

	if socket.get_ready_state() == WebSocketPeer.STATE_OPEN:
		while socket.get_available_packet_count():
			var msg = socket.get_packet().get_string_from_ascii()
			log_message(msg)
			var idea = JSON.parse_string(msg)
			manage(idea)


func _exit_tree():
	socket.close()
	tcp_server.stop()


func _on_button_pong_pressed():
	socket.send_text("Pong")

func manage(idea):
	if idea["action"] == "lookaround":
		# play_animation(idea["action"])
		pass
	elif idea["action"] == "move":
		var direction = idea["direction"]
		#move_instruction["direction"] = "move_forward"
		var original_position = position
		move_flag = true
	return "Done!"