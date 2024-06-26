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
			-- Initialize coordinates to random
		local
			 rnd: RANDOMNUMBERGENERATOR
		do
			create rnd.make
			x := rnd.new_random\\10
			y := rnd.new_random\\10
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

feature
	-- equals method
	equals(other : POSITION): BOOLEAN
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

feature

	set_to_random_position
		--set random position on 10x10 grid
		local
			rand : RANDOMNUMBERGENERATOR
			i : INTEGER
		do
			create rand.make

			x := (rand.new_random + 1)\\10
			y := (rand.new_random + 1)\\10

		end
end
