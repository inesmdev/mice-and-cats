initialization
initialize

	super initialize.
	
	self
		color: (self userInterfaceTheme get: #color for: #ScrollPane);
		extent: 1920@1080;
		layoutPolicy: TableLayout new;
		listDirection: #leftToRight;
		wrapDirection: #topToBottom;
		hResizing: #rigid;
		vResizing: #rigid;
		cellGap: 10;
		layoutInset: 20;
		yourself.
	
	#(processKeyStroke 'Test key stroke'
	processKeyDown 'Test key down'
	processKeyUp 'Test key up')
		groupsDo: [:selector :label |
			self addMorphBack: (ThreePhaseButtonMorph checkBox
				target: self;
				actionSelector: selector;
				label: label;
				yourself)].
	
	self processKeyStroke.