class
    CHECKERBOARD

create
    make

feature -- Entities
	cat : POSITION
	player : PLAYER
	subway_generator : SUBWAYGENERATOR
	subways : ARRAY2 [INTEGER]

feature -- game properties
	victory : BOOLEAN

feature -- Initialization

    make
        -- Initialize the checkerboard grid.
    do
        create cat.make
        create subway_generator.make
        create player.make
        subways := subway_generator.get_subways
        victory := FALSE
    end

    print_grid
        -- Print a 10x10 grid where each cell looks like +---+ |   | +---+
    local
        y, j, x, curr, xtmp, ytmp: INTEGER
    do
        from
            y := 0
        until
            y > 9
        loop
            from
                j := 0
            until
                j > 9
            loop
                io.put_string ("+---")
                j := j + 1
            end
            io.put_string ("+%N")


            from
                x := 0
            until
                x > 9
            loop
            	xtmp := x + 1
            	ytmp := y + 1 --arrays start at 1
            	curr := subways.item (ytmp, xtmp)

            	if
            		cat.equals (player.pos)
            	then
            		player.set_is_dead (TRUE)
            	end

            	if curr > 0 and player.pos.x.is_equal (x) and player.pos.y.is_equal (y) then
            		victory := true
            	end

            	if
            		-- render cat position (cat is "above" subways)
            		cat.x.is_equal (x) and cat.y.is_equal (y)
            	then
            		io.put_string ("| c ")
            	elseif
            		player.pos.x.is_equal (x) and player.pos.y.is_equal (y)
            	then
            		io.put_string ("| ")
            		io.put_string (player.name)
            		io.putstring (" ")
            	elseif
            		-- this is a subway cell
            		curr > 0
            	then
            		io.put_string ("| ")
            		io.putstring ("O")
            		io.putstring (" ")
            	else
            		-- this is a normal, empty cell
            		io.put_string ("|   ")
            	end
                x := x + 1
            end
            io.put_string ("|%N")
            y := y + 1

        end
        from
        	j := 0
        until
            j > 9
        loop
        	io.put_string ("+---")
            j := j + 1
        end
        io.put_string ("+%N")
    end



feature -- change positions of entities
	print_frame
		local
			random : RANDOMNUMBERGENERATOR
			i : INTEGER
		do
			-- move cat
			create random.make
			i := random.new_random\\10\\4 --gives a number between 0 and 3 -> one for each possible direction
			if i = 0 then
				if cat.x > 0 then
					cat.set_x (cat.x - 1)
				end

            elseif i = 1 then
            	if cat.y > 0 then
            		cat.set_y(cat.y - 1)
            	end

            elseif i = 2 then
            	if cat.y < 9 then
            		cat.set_y (cat.y + 1)
            	end

            elseif i = 3 then
            	if cat.x < 9 then
            		cat.set_x (cat.x + 1)
            	end
            end
			print_grid
		end

feature
	-- move entities
	move_player(key : CHARACTER)
		do
			if key = 'a' then
				if player.pos.x > 0 then
					player.pos.set_x (player.pos.x - 1)
				end

            elseif key = 'w' then
            	if player.pos.y > 0 then
            		player.pos.set_y(player.pos.y - 1)
            	end

            elseif key = 's' then
            	if player.pos.y < 9 then
            		player.pos.set_y (player.pos.y + 1)
            	end

            elseif key = 'd' then
            	if player.pos.x < 9 then
            		player.pos.set_x (player.pos.x + 1)
            	end
            else
            	--invalid command
            	io.put_string ("Invalid command. Try again.%N")
            end

		end

invariant
	--ensure that cat and player are always within bounds
	cat.y < 10 and cat.y >= 0
	cat.x < 10 and cat.y >= 0
	player.pos.x < 10 and player.pos.x >= 0
	player.pos.y < 10 and player.pos.y >= 0
end
