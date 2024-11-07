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
@onready var space : PhysicsDirectSpaceState3D = get_world_3d().direct_space_state
@onready var mesh : NavigationMesh = get_node("/root/Node3D/NavigationRegion3D").navigation_mesh

var old_region = -1
var end_communication = true

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
		
		update_region()
	
	if navigator.is_navigation_finished():
		if not end_communication:
			var msg = {"type": "inform", "code": "gained"}
			ws.send_text(JSON.stringify(msg))
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
	var sight : Dictionary = {}
	sight[ 'sender' ]  = 'body'
	sight[ 'receiver' ] = 'vesna'
	sight[ 'type' ] = 'sight'
	var obj : Dictionary = {}
	obj[ 'sight' ] = body
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
		walk( target )
		
func walk( target ):
	Log.info("I have to move ", target)
	if target == 'random':
		navigator.set_target_position(position + Vector3(0.0, 0.0, 15.0))
	
func update_region() -> void:
	var current_region : int = get_current_region()
	if current_region != old_region:
		var rcc : Dictionary = {}
		rcc[ 'sender' ] = 'body'
		rcc[ 'receiver' ] = 'vesna'
		rcc[ 'type' ] = 'rcc'
		var data : Dictionary = {}
		data[ 'current' ] = current_region
		rcc[ 'data' ] = data
		ws.send_text(JSON.stringify(rcc))
		old_region = current_region

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

func get_current_region() -> int:
	var vertices = mesh.get_vertices()
	var idx : int = nearest_vertex(vertices)
	var adj_triangles : Array = triangles_with_vertex(idx)
	for i in adj_triangles:
		if triangle_contains_me(i):
			return i
	return -1
