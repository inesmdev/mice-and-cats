initialization
processKeyDown

	self setProperty: #eventCheck toValue: [:evt | evt isKeyDown].
	self checkButton: 2.
	self clear.