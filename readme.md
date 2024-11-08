# VEsNA

JSON formatted messages.

## Vesna2Body

### Move

```json
{
    "sender": "vesna",
    "receiver": "body",
    "type": "walk",
    "data": {
        "target": "random"
    }
}
```

`target` can be either `random` either a **region name** to reach.

## Body2Vesna

### Sight

```json
{
    "sender": "body",
    "receiver": "vesna",
    "type": "sight",
    "data": {
        "sight": obj_name
    }
}
```

This message should be sent inside a `_on_collision_enter()` function of the sight. When an object collides check raycast and if necessary send it.

### Region Connection Calculus

```json
{
    "sender": "body",
    "receiver": "vesna",
    "type": "rcc",
    "data": {
        "current": current_triangle
    }
}
```

The `current_triangle` is the current triangle in the NavigationMesh.

