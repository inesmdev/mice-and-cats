class
    CHECKERBOARD

create
    make

feature -- Initialization

    make
        -- Initialize the checkerboard grid.
    do
        -- No initialization needed in this example.
    end

    print_grid
        -- Print a 9x9 grid where each cell looks like +---+ |   | +---+
    local
        i, j, k: INTEGER
    do
        from
            i := 1
        until
            i > 9
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
                k := 1
            until
                k > 9
            loop
                io.put_string ("|   ")
                k := k + 1
            end
            io.put_string ("|%N")
            i := i +1

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
