note
	description: "Summary description for {PLAYER}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	PLAYER

inherit
	SETTINGS

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
			name := "P"
			is_underground := FALSE
			is_dead := FALSE
		end

feature --setter
	set_pos(para : POSITION)
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

	set_name (a_par: STRING)
		require
			a_par_not_Void: a_par /= VOID
		do
			name := a_par
		end

feature {ANY}

	move (i: INTEGER)
		require
			i_in_range: i >= 0 and then i <= 3
		do
			if i = 0 then
						if pos.x > 1 then
							pos.set_x (pos.x - 1)
						end

		            elseif i = 1 then
		            	if pos.y > 1 then
		            		pos.set_y(pos.y - 1)
		            	end

		            elseif i = 2 then
		            	if pos.y < game_board_height then
		            		pos.set_y (pos.y + 1)
		            	end

		            elseif i = 3 then
		            	if pos.x < game_board_width then
		            		pos.set_x (pos.x + 1)
		            	end

		            else
		            	--invalid command
		            	io.put_string ("This should never happen.%N")
		            end
		end

end
