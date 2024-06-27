initialization
processKeyUp

	self setProperty: #eventCheck toValue: [:evt | evt isKeyUp].
	self checkButton: 3.
	self clear.