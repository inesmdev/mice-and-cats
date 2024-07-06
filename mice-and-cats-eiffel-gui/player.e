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
	name: STRING
	pos: POSITION
	is_underground: BOOLEAN
	is_dead: BOOLEAN

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
	set_pos (para: POSITION)
		do
			pos := para
		end

	set_is_underground (para: BOOLEAN)
		do
			is_underground := para
		end

	set_is_dead (para: BOOLEAN)
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

	move (i: INTEGER; board: CHECKERBOARD)
		require
			i_in_range: i >= 0 and then i <= 3
		local
			new_x, new_y: INTEGER
			is_move_legal, is_ug, same_subway: BOOLEAN
			new_tile, old_tile: INTEGER
		do
			new_x := pos.x
			new_y := pos.y

			if i = 0 then
				if pos.x > 1 then
					new_x := new_x - 1
				end
			elseif i = 1 then
				if pos.y > 1 then
					new_y := new_y - 1
				end
			elseif i = 2 then
				if pos.y < board.height then
					new_y := new_y + 1
				end
			elseif i = 3 then
				if pos.x < board.width then
					new_x := new_x + 1
				end
			else
					--invalid command
				io.put_string ("This should never happen.%N")
			end

			is_move_legal := FALSE
			if pos.x /= new_x or pos.y /= new_y then
				is_ug := is_underground
				old_tile := board.grid.item (pos.y, pos.x)
				new_tile := board.grid.item (new_y, new_x)

				if not is_ug then
					is_move_legal := TRUE
					if new_tile < 0 then
						is_ug := TRUE
					end
				else
					same_subway := new_tile = old_tile or new_tile = - old_tile
					is_move_legal := old_tile < 0 or same_subway
					is_ug := new_tile < 0 or same_subway
				end

				if is_move_legal then
					pos.set_x (new_x)
					pos.set_y (new_y)
					is_underground := is_ug
				end

			end
		end

end
