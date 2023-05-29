extends Node

var animation_player

# Called when the node enters the scene tree for the first time.
func _ready():
	animation_player = get_node("AnimationPlayer")
	animation_player.connect("animation_finished", self, "_on_animation_finished")


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	pass

func _on_animation_finished(anim_name):
	if anim_name == "walk":
		var final_position = animation_player.get_node()
