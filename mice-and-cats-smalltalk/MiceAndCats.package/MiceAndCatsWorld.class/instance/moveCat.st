as yet unclassified
moveCat
	|delta|
	mousePos underground ifFalse: [
		delta := ((mousePos xy - catPos xy) max: ((-1)@(-1))) min: (1@1).
		catPos xy: (catPos xy + delta) .
	]