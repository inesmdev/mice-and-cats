stepping and presenter
step
	"regularly invoked update method"
	||
	movement ~= (0@0) ifTrue: [
		world moveMouse: movement.
		movement := 0@0
	]
	