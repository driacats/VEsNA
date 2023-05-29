extends Node

const PORT : int = 9080
const ADDRESS : String = "127.0.0.1"
var server = TCPServer.new()
var cube_counter : int = 0
var center : Vector3 = Vector3(0.0, 0.0, 0.0)
var left : Vector3 = Vector3(0.0, 0.0, 0.0)
var right : Vector3 = Vector3(0.0, 0.0, 0.0)
var behind : Vector3 = Vector3(0.0, 0.0, 0.0)
var front : Vector3 = Vector3(0.0, 0.0, 0.0)
var obj_counters = {}
var obj_meshes = {}
var animation_library = AnimationLibrary.new()

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
		
	obj_meshes["cube"] = BoxMesh.new()
	obj_meshes["table"] = load("res://objects/table.obj")
	obj_meshes["chair"] = load("res://objects/chair.obj")
	load_animation_library()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta):
	if server.is_connection_available(): # If there is a client connected
		var client = server.take_connection() # Take the connection
		if client.get_available_bytes() > 0: # If a message is sent
			var instruction_string = str(client.get_string(client.get_available_bytes())) # Get the instruction string from the client
			var instruction = parse_instruction(instruction_string) # From JSON string to Vector3 position
			var obj_name = instruction[0]
			var position = instruction[1]
			var action = instruction[2]
			var new_name
			if action == "object":
				new_name = add_object(obj_name, position)
			elif action == "actor":
				var port = JSON.parse_string(instruction_string)["port"]
				new_name = add_actor(obj_name, position, port)
			elif action == "remove":
				new_name = remove_object(obj_name)
			client.put_string(new_name)
		
func load_animation_library():
	var animations = DirAccess.open("res://animations")
	if animations:
		animations.list_dir_begin()
		var gltf = animations.get_next()
		while gltf != "":
			if gltf.ends_with(".gltf"):
				var packed_path = animations.get_current_dir() + "/" + gltf
				var packed = load(packed_path)
				var body = packed.instantiate()
				var animation = body.get_node("AnimationPlayer").get_animation("Armature|mixamocom|Layer0").duplicate()
				for i in range(animation.get_track_count()):
					var new_path = NodePath(str(animation.track_get_path(i)).replace("Armature/", ""))
					animation.track_set_path(i, new_path)
				animation_library.add_animation(gltf.replace(".gltf", ""), animation)
			gltf = animations.get_next()

func check_position(position):
	for child in get_children():
		if child.position == position:
			return false
	return true

# Function that given a position adds a cube in that position (Vector3)
func add_object(obj_name, position):
	if obj_name in obj_counters:
		obj_counters[obj_name] += 1
	else:
		obj_counters[obj_name] = 0
	var new_name = obj_name + str(obj_counters[obj_name])
	var new_body = RigidBody3D.new() # Create a new RigidBody3D
	new_body.position = position # Assign the position
	new_body.mass = 1.0
	new_body.set_name(new_name)
	
	var new_material = PhysicsMaterial.new() # Create a new Physics Material
	new_material.bounce = 0.3 # Set a certain low value for bounce
	new_body.set_physics_material_override(new_material) # Set the Physics Material in the RigidBody3D
	
	var new_mesh = MeshInstance3D.new() # Create a mesh
	new_mesh.mesh = obj_meshes[obj_name]
	new_body.add_child(new_mesh) # Add it to the scene
	
	var collision_shape = ConvexPolygonShape3D.new()
	collision_shape.set_points(new_mesh.mesh.get_faces())
	var collision_shape_owner = CollisionShape3D.new()
	collision_shape_owner.shape = collision_shape
	new_body.add_child(collision_shape_owner)
	get_node("Objects").add_child(new_body) # Add the RigidBody to scene
	return new_name
	
func add_actor(actor_name, position, port):
	var new_actor = load("res://actor.tscn").instantiate()
	new_actor.position = position
	new_actor.name = actor_name
	
	var packed_standing_body = load("res://animations/standing.gltf")
	var packed_standing = packed_standing_body.instantiate()
	var animation_player = packed_standing.get_node("AnimationPlayer").duplicate()
	animation_player.add_animation_library("vesna_library", animation_library)
	animation_player.name = "AnimationPlayer"
	animation_player.set_physics_process(true);
	new_actor.add_child(animation_player)
	
	var actor_script = load("res://actor.gd")
	new_actor.set_script(actor_script)
	new_actor.set_meta("port", int(port))
	
	get_node("Actors").add_child(new_actor)
	
	#get_node("RootMotionView/AnimationTree").anim_player = get_node("Actors").get_node(actor_name).get_node("AnimationPlayer").get_path()
	#get_node("RootMotionView/AnimationTree").active = true
	
	return actor_name
	
# Function that given a name of an object removes it
# TODO: Add a "all" parameter to empty the scene
func remove_object(obj_name):
	if get_node("Objects").has_node(obj_name):
		var obj = get_node("Objects").get_node(obj_name)
		obj.free()
	return obj_name

func compute_global_position(instruction):
	var position = Vector3(0.0, 1.5, 0.0)
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
	var position = Vector3(0.0, 1.5, 0.0)
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
	if instruction["globAddition"]:
		return [instruction["objName"], compute_global_position(instruction), "object"]
	elif instruction["relAddition"]:
		if not has_node(instruction["objRel"]):
			return "ERROR"
		return [instruction["objName"], compute_relative_position(instruction), "object"]
	elif instruction["actorAddition"]:
		if instruction["posX"]:
			return [instruction["actorName"], compute_global_position(instruction), "actor"]
		elif instruction["posRel"]:
			return [instruction["actorName"], compute_relative_position(instruction), "actor"]
	elif instruction["removal"] == "true":
		return [instruction["objRel"], null, "remove"]
	
