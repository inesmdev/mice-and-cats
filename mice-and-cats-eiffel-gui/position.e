note
	description: "The position given in coordinates in a 2d grid"
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	POSITION

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
			-- Initialize coordinates to zero
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
	equals (other: POSITION): BOOLEAN
			-- equals method
		do
			if
				x = other.x and y = other.y
			then
				Result := TRUE
			else
				Result := FALSE
			end
		end

end
