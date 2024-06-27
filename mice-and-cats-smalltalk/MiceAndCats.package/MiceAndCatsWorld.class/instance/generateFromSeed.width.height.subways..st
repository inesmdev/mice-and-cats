as yet unclassified
generateFromSeed: seed width: w height: h subways: s
	"Generates a world with a given size and number of subways"
	| |
	width := w.
	height := h.
	grid := Array new: (w * h).
	rng := Random seed: seed.
	grid at: 1 put: 1.
	grid at: 16 put: 1.
	catPos := MiceAndCatsPosition new xy: 3@4; underground: false.
	mousePos := MiceAndCatsPosition new xy: 9@8; underground: false.