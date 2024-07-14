note
	description: "The position given in coordinates in a 2d grid"
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	POSITION

inherit
	ANY
		redefine
			is_equal
		end

create
	make,
	make_at

feature -- Coordinates
	x: INTEGER
	y: INTEGER

feature -- Initialization
	make
			-- Initialize coordinates to zero
		do
			x := 0
			y := 0
		end
	make_at (at_x, at_y: INTEGER)
			-- Initialize at specific coordinates
		do
			x := at_x
			y := at_y
		end

feature --setter
	set_x (para: INTEGER)
		do
			x := para
		end

	set_y (para: INTEGER)
		do
			y := para
		end

feature
	-- equals method
	is_equal (other: POSITION): BOOLEAN
			-- equals method
			-- covariant redefinition of argument
		do
			Result := standard_is_equal (other)
		end

end
