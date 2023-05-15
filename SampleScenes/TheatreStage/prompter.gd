extends Node

const PORT = 9080
const ADDRESS = "127.0.0.1"
var server = TCPServer.new()
var cube_counter = 0
var center = Vector3(0.0, 0.0, 0.0)
var left = Vector3(0.0, 0.0, 0.0)
var right = Vector3(0.0, 0.0, 0.0)
var behind = Vector3(0.0, 0.0, 0.0)
var front = Vector3(0.0, 0.0, 0.0)

# Called when the node enters the scene tree for the first time.
func _ready():
	server.listen(PORT, ADDRESS) # Make the server start
	if has_node("StageStructure/Stage/Stage"):
		var stage = get_node("StageStructure/Stage/Stage")
		center = stage.position
		var dims = stage.scale
		left[0] = center[0] - dims[0] / 4
		right[0] = center[0] + dims[0] / 4
		behind[2] = center[2] - dims[2] / 4
		front[2] = center[2] + dims[2] / 4

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta):
	if server.is_connection_available(): # If there is a client connected
		var client = server.take_connection() # Take the connection
		if client.get_available_bytes() > 0: # If a message is sent
			var instruction_string = str(client.get_string(client.get_available_bytes())) # Get the instruction string from the client
			var position = parse_instruction(instruction_string) # From JSON string to Vector3 position
			if position is String:
				if position != "ERROR":
					remove_object(position)
					client.put_string(position)
				else:
					client.put_string("ERROR")
			elif check_position(position): # If the position is available
				var new_name = add_object(position) # Add the object in the given position
				client.put_string(new_name)

func check_position(position):
	for child in get_children():
		if child.position == position:
			return false
	return true

# Function that given a position adds a cube in that position (Vector3)
func add_object(position):
	var new_body = RigidBody3D.new() # Create a new RigidBody3D
	new_body.position = position # Assign the position
	new_body.mass = 1.0
	var new_name = "cube" + str(cube_counter)
	new_body.set_name(new_name)
	cube_counter += 1
	var new_material = PhysicsMaterial.new() # Create a new Physics Material
	new_material.bounce = 0.3 # Set a certain low value for bounce
	new_body.set_physics_material_override(new_material) # Set the Physics Material in the RigidBody3D
	add_child(new_body) # Add the RigidBody to scene
	# Add a table start
	#var new_mesh = MeshInstance3D.new() # Create a mesh
	#new_mesh.mesh = load("res://table.obj")
	#new_mesh.create_trimesh_collision()
	#new_body.add_child(new_mesh) # Add it to the scene
	# Add a table end
	var new_mesh = MeshInstance3D.new() # Create a mesh
	new_mesh.mesh = BoxMesh.new()
	#new_mesh.create_trimesh_collision()
	new_body.add_child(new_mesh) # Add it to the scene
	#new_body.set_scale(Vector3(2.0, 2.0, 2.0))
	var new_collision = CollisionShape3D.new() # Crea a new CollisionShape
	new_collision.shape = BoxShape3D.new() # Assign it a Box Collision Shape 
	new_body.add_child(new_collision) # Add it to the scene
	return new_name
	
# Function that given a name of an object removes it
# TODO: Add a "all" parameter to empty the scene
func remove_object(obj_name):
	if has_node(obj_name):
		var cube = get_node(obj_name)
		cube.free()

func compute_global_position(instruction):
	var position = Vector3(0.0, 2.0, 0.0)
	if instruction["posX"] == "right":
		position += right
	elif instruction["posX"] == "left":
		position += left
	if instruction["posY"] == "front":
		position += front
	elif instruction["posY"] == "behind":
		position += behind
	return position

func compute_relative_position(instruction):
	var position = Vector3(0.0, 2.0, 0.0)
	var rel_node = get_node(instruction["objRel"])
	position += rel_node.position
	if instruction["posRel"] == "right_of":
		position += Vector3(1.25, 0.0, 0.0)
	elif instruction["posRel"] == "left_of":
		position -= Vector3(1.25, 0.0, 0.0)
	elif instruction["posRel"] == "in_front_of":
		position += Vector3(0.0, 0.0, 1.25)
	elif instruction["posRel"] == "behind_of":
		position -= Vector3(0.0, 0.0, 1.25)
	return position

# Function that given a JSON string containing the instructions for the operation to perform it makes it ready to use for Godot
func parse_instruction(instruction_string):
	var instruction = JSON.parse_string(instruction_string)
	if instruction["globAddition"] == "true":
		return compute_global_position(instruction)
	elif instruction["relAddition"] == "true":
		if not has_node(instruction["objRel"]):
			return "ERROR"
		return compute_relative_position(instruction)
	elif instruction["removal"] == "true":
		return instruction["objRel"]
	
