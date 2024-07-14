as yet unclassified
moveMouse: delta
	"moves the mouse"

	| p |
	p := mousePos xy copy + delta.
	
	(p x >= 0 and: p y >= 0 and: p x < width and: p y < height) ifTrue: [ |oldTile newTile isMoveLegal isUnderground|
		oldTile := self tileAt: mousePos xy.
		newTile := self tileAt: p.
		isUnderground := mousePos underground.
		
		mousePos underground ifFalse: [
			isMoveLegal := true.
			newTile < 0 ifTrue: [
				isUnderground := true.
			]
		] ifTrue: [ | sameSubway |
			sameSubway := (newTile = oldTile) or: (newTile negated = oldTile).
			isMoveLegal := (oldTile < 0) or: sameSubway.
			isUnderground := (newTile < 0) or: sameSubway.
		].
		
		isMoveLegal ifTrue: [
			mousePos underground: isUnderground.
			mousePos xy: p
		].
	].
	
	