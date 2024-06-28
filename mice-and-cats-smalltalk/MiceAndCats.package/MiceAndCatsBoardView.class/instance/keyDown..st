event handling
keyDown: evt
	|k dx dy|

	"Transcript showln: evt keyString."
	dx := 0.
	dy := 0.
	
	k := evt keyString.
	(k = 'w' or: k = '<up>') ifTrue: [ dy := -1 ].	
	(k = 'a' or: k = '<left>') ifTrue: [ dx := -1 ].
	(k = 's' or: k = '<down>') ifTrue: [ dy := 1 ].	
	(k = 'd' or: k = '<right>') ifTrue: [ dx := 1 ].

	"Transcript showln: (dx@dy)."
	dx @ dy ~= (0@0) ifTrue: [
		movement := dx @ dy.
	]
	