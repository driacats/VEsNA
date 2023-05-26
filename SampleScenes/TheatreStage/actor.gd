extends Node

const ADDRESS : String = "127.0.0.1"
var server = TCPServer.new()
var view_cone
var objects = {}

# Called when the node enters the scene tree for the first time.
func _ready():
	var PORT = get_meta("port")	
	server.listen(PORT, ADDRESS) # Make the server start
	view_cone = get_node("Skeleton3D").get_node("BoneAttachment3D").get_node("Area3D")

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	if not get_node("AnimationPlayer").is_playing():
		get_node("AnimationPlayer").play("vesna_library/standing")
	if server.is_connection_available(): # If there is a client connected
		var client = server.take_connection() # Take the connection
		if client.get_available_bytes() > 0: # If a message is sent
			var instruction_string = str(client.get_string(client.get_available_bytes())) # Get the instruction string from the client
			var instruction = JSON.parse_string(instruction_string)
			var answer = analyze_instruction(instruction)
			print(instruction_string)
			print(answer)
			client.put_string(answer)
	
func _physics_process(delta):
	var objects_in_sight = view_cone.get_overlapping_bodies()
	var new_objects_local = false
	for obj in objects_in_sight:
		var parent = obj.get_parent()
		if parent.name == "Objects":
			if obj.name not in objects:
				objects[obj.name] = obj.position
			elif obj.position != objects[obj.name]:
				objects[obj.name] = obj.position
	
func analyze_instruction(instruction):
	if instruction["type"] == "perform":
		return perform_action(instruction)
	elif instruction["type"] == "request":
		return answer_request(instruction)
	return "No action available"
		
func perform_action(instruction):
	play_animation(instruction["action"])
	return "Done!"

func answer_request(instruction):
	if instruction["request"] == "eyes":
		return JSON.stringify(objects)

func play_animation(animation_name):
	get_node("AnimationPlayer").play("vesna_library/" + animation_name)
