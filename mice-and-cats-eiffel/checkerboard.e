class
    CHECKERBOARD

create
    make

feature -- Entities
	cat : POSITION
	player : PLAYER
	subway_generator : SUBWAYGENERATOR
	subways : ARRAY2 [INTEGER]

feature -- Initialization

    make
        -- Initialize the checkerboard grid.
    do
        create cat.make
        create subway_generator.make
        create player.make
        subways := subway_generator.get_subways
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
            		io.put_string ("player died")
            	end

            	if
            		-- render cat position (cat is "above" subways)
            		cat.x = x and cat.y = y
            	then
            		io.put_string ("| c ")
            	elseif
            		player.pos.x = x and player.pos.y = y
            	then
            		io.put_string ("| ")
            		io.put_string (player.name)
            		io.putstring (" ")
            	elseif
            		-- this is a subway cell
            		curr > 0
            	then
            		io.put_string ("| ")
            		io.putint (curr)
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
		random_int : INTEGER
	do
		create random.make
		random_int := random.get_random
		cat.set_x(random_int\\10) -- get last digit
		cat.set_y(random_int//10) -- get second last digit
		print_grid
	end

feature
	-- move player
	move_player(key : CHARACTER)
		do
			if key = 'a' then
				if player.pos.x > 1 then
					player.pos.set_x (player.pos.x - 1)
				end

            elseif key = 'w' then
            	if player.pos.y > 1 then
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
end
