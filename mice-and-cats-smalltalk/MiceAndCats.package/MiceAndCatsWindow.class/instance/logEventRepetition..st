event handling
logEventRepetition: evt

	| label lastEvents box |
	(self submorphs last hasProperty: #repetition)
		ifTrue: [box := self submorphs last. label := box submorphs first]
		ifFalse: [
			box := Morph new
				setProperty: #repetition toValue: true;
				color: Color transparent;
				layoutPolicy: TableLayout new;
				hResizing: #shrinkWrap;
				vResizing:#shrinkWrap;
				yourself.
			label := '' asText asMorph lock.
			box addMorph: label.
			box setProperty: #event toValue: (OrderedCollection with: self lastEvent).
			self addMorphBack: box].

	lastEvents := box valueOfProperty: #event.
	lastEvents add: evt copy.
	box setProperty: #event toValue: lastEvents.

	label newContents: (('x ', (lastEvents size)) asText
		addAttribute: (TextFontReference toFont: Preferences standardButtonFont);
		yourself).
	box balloonText: ('{1}{2}'  format: {
		lastEvents size > 10 ifTrue: ['... {1} older events and:\' translated withCRs format: {lastEvents size - 10}] ifFalse: [''].
		(lastEvents last: (10 min: lastEvents size)) joinSeparatedBy: String cr.
		}).
			
	box
		on: #mouseEnter send: #handleEvent:emphasize: to: self;
		on: #mouseLeave send: #handleEvent:deemphasize: to: self;
		on: #mouseDown send: #handleEvent:inspect: to: self.