extends CharacterBody3D

const ADDRESS : String = "127.0.0.1"
var server = TCPServer.new()
var view_cone
var objects = {}

var move_flag = false
var move_instruction = {}

@export var speed = 7
@export var fall_acceleration = 50
var target_velocity = Vector3.ZERO

# Called when the node enters the scene tree for the first time.
func _ready():
	# var PORT = get_meta("port")
	var PORT = 8080
	server.listen(PORT, ADDRESS) # Make the server start
	view_cone = get_node("Rig").get_node("Skeleton3D").get_node("Rogue_Head_Hooded").get_node("Area3D")

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	if not get_node("AnimationPlayer").is_playing():
		get_node("AnimationPlayer").play("Idle")
	if server.is_connection_available(): # If there is a client connected
		var client = server.take_connection() # Take the connection
		if client.get_available_bytes() > 0: # If a message is sent
			var instruction_string = str(client.get_string(client.get_available_bytes())) # Get the instruction string from the client
			var instruction = JSON.parse_string(instruction_string)
			var answer = analyze_instruction(instruction)
			client.put_string(answer)
	
func _physics_process(delta):
	var direction = Vector3.ZERO
	
	if move_flag and move_instruction["direction"] == "forward":
		direction.z = direction.z + 1
	if move_flag and move_instruction["direction"] == "back":
		direction.z = direction.z - 1
	if move_flag and move_instruction["direction"] == "right":
		direction.x = direction.x + 1
	if move_flag and move_instruction["direction"] == "left":
		direction.x = direction.x - 1
	
	if direction != Vector3.ZERO:
		direction = direction.normalized()
		look_at(position - direction, Vector3.UP)
		$AnimationPlayer.speed_scale = 0.5
		$AnimationPlayer.play("vesna_library/walk")
	else:
		$AnimationPlayer.speed_scale = 1
		
	target_velocity.x = direction.x * speed
	target_velocity.z = direction.z * speed
	
	if not is_on_floor():
		target_velocity.y = target_velocity.y - (fall_acceleration * delta)
		
	velocity = target_velocity
	move_and_slide()
	see()
	#if move_flag and position.z - move_instruction["original_position"].z > 2:
	if move_flag and position.distance_to(move_instruction["original_position"]) > 2:
		move_flag = false
		$AnimationPlayer.stop()
				
func see():
	var objects_in_sight = view_cone.get_overlapping_bodies()
	for obj in objects_in_sight:
		var parent = obj.get_parent()
		if parent.name == "Furniture":
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
	if instruction["action"] == "lookaround":
		play_animation(instruction["action"])
	elif instruction["action"] == "move":
		move_instruction["direction"] = instruction["direction"]
		#move_instruction["direction"] = "move_forward"
		move_instruction["original_position"] = position
		move_flag = true
	return "Done!"

func answer_request(instruction):
	if instruction["request"] == "eyes":
		return JSON.stringify(objects)

func play_animation(animation_name):
	get_node("AnimationPlayer").play("vesna_library/" + animation_name)
