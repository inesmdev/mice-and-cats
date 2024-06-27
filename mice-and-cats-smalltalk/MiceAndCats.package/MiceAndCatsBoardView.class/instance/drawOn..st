drawing
drawOn: aCanvas
      |r color cellSize offset|
	super drawOn: aCanvas.
	
	cellSize := (self width // (world width)) min: (self height // (world height)).
      offset := self extent - (world width * cellSize @ (world height * cellSize)) // 2.
	
	aCanvas
		drawString: 'Move your mouse cursor to here and start typing. Try modifiers, too.' translated
		at: self topLeft
		font: Preferences standardButtonFont
		color: Color gray "right parenthesis expected ->".

	"Draw the grid"	
	aCanvas translateBy: (self topLeft + offset) during: [ :c |		
		0 to: (world height - 1) do: [ :y |
			0 to: (world width - 1) do: [ :x | 
				color := (x + y \\ 2 = 0) ifTrue: [Color black] ifFalse: [Color gray].
			      r := ((x*cellSize)@(y*cellSize) extent: cellSize@cellSize).
				c fillRectangle: r color: color.			
			]
		]
	]

	 
