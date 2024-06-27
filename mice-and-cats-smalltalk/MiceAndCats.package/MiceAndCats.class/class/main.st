as yet unclassified
main
	"Run the game"

	|world|
	Transcript showln: 'Running MiceAndCats'.
	world := MiceAndCatsWorld new generateFromSeed: 42 width: 22 height: 9 subways: 5.
	
	MiceAndCatsBoardView new 
           setWorld: world; 
           openInWindowLabeled: 'MiceAndCats'.
	
	