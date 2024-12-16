extends CharacterBody3D

var speed = 6
var acceleration = 10

const PORT = 9080
var tcp_server := TCPServer.new()
var ws := WebSocketPeer.new()

@onready var navigator : NavigationAgent3D = $NavigationAgent3D
@onready var animator : AnimationPlayer = $AnimationPlayer
@onready var front_sight : Area3D = $Rig/Skeleton3D/Rogue_Head_Hooded/FrontalView
@onready var lateral_sight : Area3D = $Rig/Skeleton3D/Rogue_Head_Hooded/LateralView
@onready var head = $Rig/Skeleton3D/Rogue_Head_Hooded
@onready var space : PhysicsDirectSpaceState3D = get_world_3d().direct_space_state
@onready var mesh : NavigationMesh = get_node("/root/Node3D/NavigationRegion3D").navigation_mesh

var old_triangle = -1
var end_communication = true

var region_dict : Dictionary = {}
var current_region = "region0";

func _ready() -> void:
	if tcp_server.listen( PORT ) != OK:
		Log.err( "Unable to start the server." )
		set_process( false )
	front_sight.connect( 'body_entered', _on_area_body_entered )
	lateral_sight.connect( 'body_entered', _on_area_body_entered )


func _physics_process( delta: float ) -> void:
	while tcp_server.is_connection_available():
		var conn : StreamPeerTCP = tcp_server.take_connection()
		assert( conn != null)
		ws.accept_stream( conn )
	
	ws.poll()
	
	if ws.get_ready_state() == WebSocketPeer.STATE_OPEN:
		while ws.get_available_packet_count():
			var msg : String = ws.get_packet().get_string_from_ascii()
			Log.info("Received message ", msg)
			var intention : Dictionary = JSON.parse_string( msg )
			manage( intention )
		
		update_triangle()
	
	if navigator.is_navigation_finished():
		if not end_communication:
			var log : Dictionary = {}
			log[ 'sender' ] = 'body'
			log[ 'receiver' ] = 'vesna'
			log[ 'type' ] = 'signal'
			var msg : Dictionary = {}
			msg[ 'type' ] = 'movement'
			msg[ 'status' ] = 'completed'
			msg[ 'reason' ] = 'destination_reached'
			log[ 'data' ] = msg
			Log.info("Motion ", JSON.stringify(log))
			ws.send_text(JSON.stringify(log))
			end_communication = true
		return
	
	var direction = Vector3()
	direction = navigator.get_next_path_position() - global_position
	direction = direction.normalized()
	
	velocity = velocity.lerp(direction * speed, acceleration * delta)
	
	move_and_slide()
	
	
func _exit_tree() -> void:
	ws.close()
	tcp_server.stop()
	
func _on_area_body_entered( body ):
	
	var space = get_world_3d().direct_space_state
	var ray = PhysicsRayQueryParameters3D.create(head.global_position, head.global_position - head.global_position)
	if space.intersect_ray( ray ):
		var sight : Dictionary = {}
		sight[ 'sender' ]  = 'body'
		sight[ 'receiver' ] = 'vesna'
		sight[ 'type' ] = 'sight'
		var obj : Dictionary = {}
		obj[ 'sight' ] = body.name
		obj[ 'id' ] = body.get_instance_id()
		sight[ 'data' ] = obj
		Log.info( 'Sight ', sight )
		ws.send_text( JSON.stringify( sight ) )
			
func manage( intention ):
	var sender : String = intention['sender']
	var receiver : String = intention['receiver']
	var type : String = intention['type']
	var data : Dictionary = intention['data']
	if type == 'walk':
		var target : String = data['target']
		var id : int = data['id']
		walk( target, id )
	elif type == 'region':
		var new_region : String = data['region']
		update_region( new_region )
		
func walk( target, id ):
	Log.info("I have to move ", target)
	if target == 'random':
		# navigator.set_target_position(position + Vector3(0.0, 0.0, 8.0))
		var tr = get_current_triangle()
		Log.info("Current triangle is " + str(tr) + " and old one is " + str(old_triangle))
		if tr == -1:
			tr = old_triangle
		var adjs = get_adj_triangles( tr )
		navigator.set_target_position( get_triangle_center(adjs[0]) )
	if target == 'triangle':
		navigator.set_target_position( get_triangle_center(id) )
	if target == 'door':
		var door_node = instance_from_id(id)
		var dist = door_node.position - position
		
		navigator.set_target_position( door_node.position )
	end_communication = false
	
func update_triangle() -> void:
	var current_triangle : int = get_current_triangle()
	if current_triangle != old_triangle and current_triangle != -1:
		if current_region not in region_dict:
			region_dict[current_region] = []
		if current_triangle not in region_dict[current_region]:
			region_dict[current_region].append(current_triangle)
		print(region_dict)
		old_triangle = current_triangle
		
func update_region( new_region : String) -> void:
	current_region = new_region
	if current_region not in region_dict:
		region_dict[ current_region ] = []

## MESH FUNCTIONS
func nearest_vertex(v: Array) -> int:
	var pos = position
	var nearest : Vector3 = v[0]
	var nearest_idx : int = 0
	var min_dist : float = pos.distance_to(nearest)
	
	for i in len( v ):
		var dist = pos.distance_to(v[i])
		if dist < min_dist:
			nearest = v[i]
			nearest_idx = i
			min_dist = dist
	return nearest_idx

func triangles_with_vertex( idx : int ) -> Array:
	var adjs : Array = []
	for i in range( mesh.get_polygon_count() ):
		if ( idx in mesh.get_polygon(i) ):
			adjs.append(i)
	return adjs
	
func get_adj_triangles( triangle_idx : int ) -> Array:
	var current = mesh.get_polygon( triangle_idx )
	var adj_triangles : Array = []
	for i in range( mesh.get_polygon_count() ):
		var tr_i = mesh.get_polygon(i)
		if ( current[0] in tr_i ):
			if ( current[1] in tr_i || current[2] in tr_i ):
				adj_triangles.append(i)
		elif ( current[1] in tr_i ):
			if ( current[0] in tr_i || current[2] in tr_i ):
				adj_triangles.append(i)
		elif ( current[2] in tr_i ):
			if ( current[1] in tr_i || current[1] in tr_i):
				adj_triangles.append(i)
	adj_triangles.erase(triangle_idx)
	return adj_triangles
		
func get_triangle_center( t_idx : int ) -> Vector3:
	var v_idxs : Array = mesh.get_polygon(t_idx)
	var a : Vector3 = mesh.get_vertices()[v_idxs[0]]
	var b : Vector3 = mesh.get_vertices()[v_idxs[1]]
	var c : Vector3 = mesh.get_vertices()[v_idxs[2]]
	var center_x = ( a[0] + b[0] + c[0] ) / 3
	var center_y = 0.0
	var center_z = ( a[2] + b[2] + c[2] ) / 3
	return Vector3(center_x, center_y, center_z)

func triangle_contains_me( t_idx : int ) -> bool:
	var v_idxs = mesh.get_polygon(t_idx)
	var t = []
	for v_idx in v_idxs:
		t.append( mesh.get_vertices()[v_idx] )
	var pos = position
	pos[1] = 0.0
	var ab = t[1] - t[0]
	var ac = t[2] - t[0]
	var ap = pos - t[0]
	
	var dot00 = ab.dot(ab)
	var dot01 = ab.dot(ac)
	var dot02 = ab.dot(ap)
	var dot11 = ac.dot(ac)
	var dot12 = ac.dot(ap)
	
	var inv_den = 1.0 / (dot00 * dot11 - dot01 * dot01)
	var u = (dot11 * dot02 -dot01 * dot12) * inv_den
	var v = (dot00 * dot12 - dot01 * dot02) * inv_den
	return u >= 0 and v >= 0 and (u + v) <= 1

func get_current_triangle() -> int:
	var vertices = mesh.get_vertices()
	var idx : int = nearest_vertex(vertices)
	var adj_triangles : Array = triangles_with_vertex(idx)
	for i in adj_triangles:
		if triangle_contains_me(i):
			return i
	return -1
