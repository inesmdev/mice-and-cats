as yet unclassified
moveMouse: delta
	"moves the mouse"

	| p |
	p := mousePos copy.
	p xy: (p xy + delta).
	
	(p xy x >= 0 and: p xy y >= 0 and: p xy x < width and: p xy y < height) ifTrue: [
		mousePos := p
	].
	
	