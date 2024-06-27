note
	description: "Summary description for {PLAYER}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	PLAYER

create
	make

feature -- attributes of player
	name : STRING
	pos : POSITION
	is_underground : BOOLEAN
	is_dead : BOOLEAN

feature
	-- initialization
	make
		do
			create pos.make
			pos.set_to_random_position
			name := "P"
			is_underground := FALSE
			is_dead := FALSE
		end

feature --setter
	set_pos(para : POSITION)
		require
			para.x < 10 and para.x >= 0
			para.y < 10 and para.y >= 0
		do
			pos := para
		end

	set_is_underground(para: BOOLEAN)
		do
			is_underground := para
		end

	set_is_dead(para: BOOLEAN)
		do
			is_dead := para
		end
end
