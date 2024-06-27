stepping and presenter
step
	"regularly invoked update method"
	||
	movement ~= (0@0) ifTrue: [
		world moveMouse: movement.
		movement := 0@0
	].
	world moveCat.
	
	world isDead ifTrue: [
		world generateFromSeed: 42 width: (world width + 4) height: (world height + 4) subways: (world subways + 1).
	]