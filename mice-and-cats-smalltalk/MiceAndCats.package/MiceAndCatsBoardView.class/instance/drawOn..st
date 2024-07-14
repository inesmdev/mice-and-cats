drawing
drawOn: aCanvas
      | cellSize offset|
	super drawOn: aCanvas.
	
	cellSize := (self width // (world width)) min: (self height // (world height)).
      offset := self extent - (world width * cellSize @ (world height * cellSize)) // 2.
	
	aCanvas
		drawString: 'Control: wasd or arrow keys' translated
		at: self topLeft
		font: Preferences standardButtonFont
		color: Color gray "right parenthesis expected ->".

	aCanvas translateBy: (self topLeft + offset) during: [ :c |		
		world drawOn: c cellSize: cellSize
	].
	
	self changed.
	 
