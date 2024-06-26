initialization
processKeyStroke

	self setProperty: #eventCheck toValue: [:evt | evt isKeystroke].
	self checkButton: 1.
	self clear.