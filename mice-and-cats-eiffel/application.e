note
	description: "cli-cat-mouse application root class"
	date: "$Date$"
	revision: "$Revision$"

class
	APPLICATION

inherit
	ARGUMENTS_32

create
	make

feature {NONE} -- Initialization

    make
            -- Initialize the main application.
        local
            checkerboard: CHECKERBOARD
            i : INTEGER
        do
            create checkerboard.make
            io.putstring ("NEW GAME")
            io.new_line
            checkerboard.print_grid
            from
            	i := 0
            until
            	i >= 10
            loop
            	checkerboard.print_frame
            	io.new_line
            	i := i + 1
            end

        end

end
