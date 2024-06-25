class
    CHECKERBOARD

create
    make

feature -- Entities
	cat : POSITION

feature -- Initialization

    make
        -- Initialize the checkerboard grid.
    do
        create cat.make
    end

    print_grid
        -- Print a 10x10 grid where each cell looks like +---+ |   | +---+
    local
        y, j, x: INTEGER
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
            	if cat.x = x and cat.y = y  then
            		io.put_string ("| c ")
            	else
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
end
