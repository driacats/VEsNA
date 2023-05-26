extends MeshInstance3D


# Called when the node enters the scene tree for the first time.
func _ready():
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	pass

func _physics_process(delta: float) -> void:
	var overlapping_bodies := get_overlapping_bodies()
	for body_rid in overlapping_bodies:
		var body := Object(body_rid)
		# Fai qualcosa con l'oggetto collidente...
		print("Collision with: ", body)
