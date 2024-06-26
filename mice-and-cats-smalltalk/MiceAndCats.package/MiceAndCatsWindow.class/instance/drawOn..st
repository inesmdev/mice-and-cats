drawing
drawOn: aCanvas

	super drawOn: aCanvas.
	
	aCanvas
		drawString: 'Move your mouse cursor to here and start typing. Try modifiers, too.' translated
		at: self topLeft
		font: Preferences standardButtonFont
		color: Color gray.