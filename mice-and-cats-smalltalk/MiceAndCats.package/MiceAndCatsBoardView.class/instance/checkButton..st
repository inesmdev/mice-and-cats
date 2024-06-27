initialization
checkButton: checkIndex

	1 to: 3 do: [:index |
		(self submorphs at: index)
			state: #off].
	
	(self submorphs at: checkIndex) state: #on.