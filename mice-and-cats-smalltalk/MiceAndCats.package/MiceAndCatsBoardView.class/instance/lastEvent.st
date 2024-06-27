accessing
lastEvent

	| view event |
	view := self submorphs last.
	(view hasProperty: #event) ifFalse: [^ nil].	
	event := view valueOfProperty: #event.
	event isCollection ifTrue: [event := event last].
	^ event