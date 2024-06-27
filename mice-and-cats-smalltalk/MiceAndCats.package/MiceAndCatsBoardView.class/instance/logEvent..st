event handling
logEvent: evt

	| eventMorph |
	evt = self lastEvent
		ifTrue: [^ self logEventRepetition: evt].

	eventMorph := evt asMorph.
	eventMorph
		setProperty: #event toValue: evt copy;
		balloonText: ('Click to inspect. Shift+click to explore.\\Virtual key: {8}\Virtual modifiers: {5}\\Physical key: {9}\Physical modifiers: {6}\\Key value: 0x{1} ({2}) \Key character: {3}\Key string: {4}\\{7}' translated withCRs format: {
			evt keyValue printPaddedWith: $0 to: 2 base: 16.
			evt keyValue.
			evt isKeystroke ifTrue: [evt keyCharacter printString] ifFalse: ['-'].
			evt isKeystroke ifTrue: [evt keyString printString] ifFalse: ['-'].
			(evt virtualModifiers joinSeparatedBy: ' ') asUppercase.
			(evt physicalModifiers joinSeparatedBy: ' ') asUppercase.
			evt printString.
			evt virtualKey printString.
			evt physicalKey asString printString}).
			
	eventMorph
		on: #mouseEnter send: #handleEvent:emphasize: to: self;
		on: #mouseLeave send: #handleEvent:deemphasize: to: self;
		on: #mouseDown send: #handleEvent:inspect: to: self.

	self addMorphBack: eventMorph.