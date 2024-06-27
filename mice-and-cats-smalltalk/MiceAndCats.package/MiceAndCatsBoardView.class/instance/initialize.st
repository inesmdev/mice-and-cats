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
			
	"we only need key down events"
	self setProperty: #eventCheck toValue: [:evt | evt isKeyDown].