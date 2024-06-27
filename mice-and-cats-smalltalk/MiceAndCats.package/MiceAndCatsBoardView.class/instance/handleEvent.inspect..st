actions
handleEvent: mouseEvent inspect: morph

	mouseEvent	 shiftPressed
		ifTrue: [(morph valueOfProperty: #event) explore]
		ifFalse: [(morph valueOfProperty: #event) inspect].