note
	description: "Summary description for {SUBWAYGENERATOR}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	SUBWAYGENERATOR

create
	make

feature -- class variable
	grid: ARRAY2 [INTEGER]
	subway_exits: ARRAYED_LIST [ARRAYED_LIST [POSITION]]
	goal: INTEGER
	settings: SETTINGS
	rnd: RANDOMNUMBERGENERATOR

feature -- Initialization
	make (s: SETTINGS; rand: RANDOMNUMBERGENERATOR)
	    require
			width: s.game_board_width >= 3
			height: s.game_board_height >= 3
			subways: s.number_of_subways >= 1
			segments: s.max_subway_segments >= 1
			large_enough: s.game_board_width > s.min_subway_segment_length or s.game_board_height > s.min_subway_segment_length
	        min_length: s.min_subway_segment_length >= 2
	        max_length: s.max_subway_segment_length >= s.min_subway_segment_length
		do
			settings := s
			rnd := rand
			create grid.make_filled (0, settings.game_board_height, settings.game_board_width)
			create subway_exits.make (settings.number_of_subways)
			goal := 1 -- arbitrarily pick the first subway as the goal
			create_entries
		end

feature
	create_entries
			-- create random entries
		local
			i, j, col, row, exit: INTEGER
			pos: POSITION
		do
				-- create exits lists
			from i := 1
			until i > settings.number_of_subways
			loop
				subway_exits.extend (create {ARRAYED_LIST [POSITION]}.make (10))
				i := i + 1
			end

				-- place initial segments
			from
				i := 1
			until
				i > settings.number_of_subways
			loop
				col := rnd.new_random \\ grid.width + 1
				row := rnd.new_random \\ grid.height + 1

				if grid.item (row, col) = 0 and then place_segment (col, row, i) then
					grid.put (- i, row, col)
					(subway_exits @ i).extend (create {POSITION}.make_at (col, row))
					i := i + 1
				end
			end

				-- extend subways
			from j := 1
			until j > settings.max_subway_segments
			loop
				from
					i := 1
				until
					i > settings.number_of_subways
				loop
					exit := rnd.new_random \\ (subway_exits @ i).count + 1
					pos := subway_exits @ i @ exit

					if place_segment (pos.x, pos.y, i) then
						if rnd.new_random \\ 2 = 0 then
							(subway_exits @ i).remove_i_th (exit)
							grid.put (i, pos.y, pos.x) -- don't restore exit
						else
							grid.put (- i, pos.y, pos.x) -- restore temporarily removed value
						end
					else
						grid.put (- i, pos.y, pos.x) -- restore temporarily removed value
					end

					i := i + 1
				end
				j := j + 1
			end
		end

	place_segment (from_x, from_y, subway: INTEGER): BOOLEAN
		require
			at_empty_or_exit: grid.item (from_y, from_x) = 0 or grid.item (from_y, from_x) = - subway
		local
			dx, dy, length, min_len, i: INTEGER
		do
			if rnd.new_random \\ 2 = 0 then
				dx := 1
				dy := 0
			else
				dx := 0
				dy := 1
			end
			if rnd.new_random \\ 2 = 0 then
				dx := - dx
				dy := - dy
			end

            min_len := settings.min_subway_segment_length - 1
			length := rnd.new_random \\ (settings.max_subway_segment_length.max (min_len) - min_len) + min_len + 1

				-- temporarily remove the exit
			grid.put (0, from_y, from_x)

				-- check if segment can be placed
			Result := TRUE
			from
				i := 1 -- deliberately don't check the first one
			until
				not Result or i >= length
			loop
				Result := it_and_all_neighbours_zero (from_x + dx * i, from_y + dy * i)
				i := i + 1
			end

			if Result then
					-- place segment
				from
					i := 0
				until
					i >= length - 1
				loop
					grid.put (subway, from_y + dy * i, from_x + dx * i)
					i := i + 1
				end

				grid.put (- subway, from_y + dy * i, from_x + dx * i)
				(subway_exits @ subway).extend (create {POSITION}.make_at (from_x + dx * i, from_y + dy * i))
			end
		end

	it_and_all_neighbours_zero (x, y: INTEGER): BOOLEAN
		do
			if x > 1 and y > 1 and then x < grid.width and then y < grid.height then
				Result := grid.item (y, x) = 0 and grid.item (y - 1, x) = 0 and then grid.item (y + 1, x) = 0 and then grid.item (y, x - 1) = 0 and then grid.item (y, x + 1) = 0
			end
		end

feature -- return grid of subways
	get_grid: ARRAY2 [INTEGER]
		do
			Result := grid
		end
	get_subway_exits: ARRAYED_LIST [ARRAYED_LIST [POSITION]]
		do
			Result := subway_exits
		end
	get_goal: INTEGER
		do
			Result := goal
		end

end
