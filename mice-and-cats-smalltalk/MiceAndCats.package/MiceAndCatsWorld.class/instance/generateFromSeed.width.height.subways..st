as yet unclassified
generateFromSeed: seed width: w height: h subways: s
	"Generates a world with a given size and number of subways"
	| l2 |
	width := w.
	height := h.
	subways := s.
	grid := Array new: (w * h).
	grid atAllPut: 0.
	rng := Random seed: seed.
	catPos := MiceAndCatsPosition new xy: 3@4; underground: false.
	mousePos := MiceAndCatsPosition new xy: 9@(h // 3); underground: true.
	
	l2 := h // 3 * 2.
	4 to: (w - 4) do: [ :x | 
		self tileAt: x@(h // 3) put: 1.
		self tileAt: x@l2 put: 2.
	].
	self tileAt: (3)@(h // 3) put: -1.
	self tileAt: (w - 3)@(h // 3) put: -1.
	
	0 to: 2 do: [ :x | | min max rand |
		min := 3.
		max := w - 3.
		rand := rng nextInt: (max - min + 1) + min - 1.
		self tileAt: rand@(h // 3 - 1) put: 1.
		self tileAt: rand@(h // 3 - 2) put: -1.
	].

	self tileAt: (3)@l2 put: -2.
	self tileAt: (w - 3)@l2 put: -2.




