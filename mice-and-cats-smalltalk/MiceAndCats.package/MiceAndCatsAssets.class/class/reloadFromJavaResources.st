as yet unclassified
reloadFromJavaResources
	"Reloads the assets from the java resource folder. This is intended to be called manually from the workspace."
	
	|rootDirectory imageDirectory| 
	Transcript showln: 'Select the project root folder'.
	rootDirectory := FileList2 modalFolderSelector.
	rootDirectory ifNotNil: [
		Transcript showln: rootDirectory .
		imageDirectory := rootDirectory.
		imageDirectory  := imageDirectory directoryNamed: 'mice-and-cats-java'.
		imageDirectory  := imageDirectory directoryNamed: 'src'.
		imageDirectory  := imageDirectory directoryNamed: 'main'.
		imageDirectory  := imageDirectory directoryNamed: 'resources'.
		imageDirectory  := imageDirectory directoryNamed: 'images'.
		Transcript showln: imageDirectory.
		
		Cat      := ImageReadWriter formFromStream: ((imageDirectory fileNamed: 'cat.png')  readStream) . 
		Mouse := ImageReadWriter formFromStream: ((imageDirectory fileNamed: 'mouse.png') readStream) .
		
		Cat asMorph openInWindowLabeled: 'cat'.
	] ifNil: [
		Transcript showln: 'No directory selected'
	].

	
