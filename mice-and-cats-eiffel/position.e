note
	description: "The position given in coordinates in a 2d grid"
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	POSITION

create
	make

feature -- Coordinates
	x : INTEGER
	y : INTEGER

feature -- Initialization
	make
			-- Initialize coordinates to 1,1.
		do
			x := 1
			y := 1
		end

feature --setter
	set_x(para : INTEGER)
		do
			x := para
		end

	set_y(para: INTEGER)
		do
			y := para
		end
end
