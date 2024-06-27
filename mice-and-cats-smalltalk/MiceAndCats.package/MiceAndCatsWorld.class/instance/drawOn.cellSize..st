as yet unclassified
drawOn: canvas cellSize: cellSize
	"Draws the world"
	|color r|
	0 to: (height - 1) do: [ :y |
		0 to: (width - 1) do: [ :x | 
			color := (x + y \\ 2 = 0) ifTrue: [Color gray] ifFalse: [Color gray darker].
		      r := x@y * cellSize extent: cellSize@cellSize.
			canvas fillRectangle: r color: color.			
		]
	].
	
	canvas drawImage: (MiceAndCatsAssets mouse scaledToSize: (cellSize@cellSize)) at: (mousePos xy * cellSize).
	canvas drawImage: (MiceAndCatsAssets cat scaledToSize: (cellSize@cellSize)) at: (catPos xy * cellSize).
	

