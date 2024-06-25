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
        -- Print a 9x9 grid where each cell looks like +---+ |   | +---+
    local
        y, j, x: INTEGER
    do
        from
            y := 1
        until
            y > 9
        loop
            from
                j := 1
            until
                j > 9
            loop
                io.put_string ("+---")
                j := j + 1
            end
            io.put_string ("+%N")


            from
                x := 1
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
        	j := 1
        until
            j > 9
        loop
        	io.put_string ("+---")
            j := j + 1
        end
        io.put_string ("+%N")
    end

end
