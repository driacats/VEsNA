extends Node

const ADDRESS : String = "127.0.0.1"
var server = TCPServer.new()

# Called when the node enters the scene tree for the first time.
func _ready():
	var PORT = get_meta("port")	
	server.listen(PORT, ADDRESS) # Make the server start


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	if not get_node("AnimationPlayer").is_playing():
		get_node("AnimationPlayer").play("vesna_library/standing")
	if server.is_connection_available(): # If there is a client connected
		var client = server.take_connection() # Take the connection
		if client.get_available_bytes() > 0: # If a message is sent
			var instruction_string = str(client.get_string(client.get_available_bytes())) # Get the instruction string from the client
			var instruction = JSON.parse_string(instruction_string)
			play_animation(instruction["action"])
			print(instruction_string)
			client.put_string("Received!")
		
func play_animation(animation_name):
	get_node("AnimationPlayer").play("vesna_library/" + animation_name)
