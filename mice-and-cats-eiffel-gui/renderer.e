note
	description: "An abstraction for rendering tiles"
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	RENDERER

create
	make_with_size

feature
	pixmap: EV_PIXMAP
	width, height: INTEGER

	tile_size: INTEGER
	tiles_x, tiles_y: INTEGER
	ox, oy: INTEGER
	mu: INTEGER

	make_with_size (a_width, a_height: INTEGER)
			-- initialize the renderer for a given screen size
		do
			create pixmap.make_with_size (a_width, a_height)
			grid_size (1, 1)
		end

	resize (w, h: INTEGER)
			-- Update the screen size
			-- The underlying pixmap is grown exponentially if required and never shrunk.
		do
			if width /= w or else height /= h then
				width := w
				height := h

					-- Grow pixmap exponentially if required
				if pixmap.width < w or pixmap.height < h then
					create pixmap.make_with_size (w.max (2 * pixmap.width), h.max (2 * pixmap.height))
				end

				pixmap.remove_clip_area
				pixmap.set_clip_area (create {EV_RECTANGLE}.make (0, 0, w, h))
			end
		ensure
			wide_enough: pixmap.width >= w
			tall_enough: pixmap.height >= h
		end

	clear (color: EV_COLOR)
			-- fill with a color
		do
			pixmap.set_background_color (color)
			pixmap.clear
		end

	grid_size (x, y: INTEGER)
			-- should be called at the start of every frame to initialize tile sizes for the current screen size
		require
			positive: x > 0 and y > 0
		do
			tiles_x := x
			tiles_y := y

			tile_size := (width // tiles_x).min (height // tiles_y)
			ox := (width - (tiles_x * tile_size)) // 2
			oy := (height - (tiles_y * tile_size)) // 2
			mu := tile_size // 20
		ensure
			w: ox + tiles_x * tile_size <= width
			h: oy + tiles_y * tile_size <= height
		end

	tile (x, y: INTEGER; color: EV_COLOR)
			-- render a tile
		require
			x_bounds: x > 0 and x <= tiles_x
			y_bounds: y > 0 and y <= tiles_y
		do
			pixmap.set_foreground_color (color)
			pixmap.fill_rectangle (ox + (x - 1) * tile_size, oy + (y - 1) * tile_size, tile_size, tile_size)
		end

	tile_center (x, y: INTEGER; color: EV_COLOR)
			-- render a slightly smaller tile
		require
			x_bounds: x > 0 and x <= tiles_x
			y_bounds: y > 0 and y <= tiles_y
		do
			pixmap.set_foreground_color (color)
			pixmap.fill_rectangle (ox + (x - 1) * tile_size + mu, oy + (y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)
		end

	entity (x, y: INTEGER; color: EV_COLOR)
			-- render an entity
		require
			x_bounds: x > 0 and x <= tiles_x
			y_bounds: y > 0 and y <= tiles_y
		do
			pixmap.set_foreground_color (color)
			pixmap.fill_ellipse (ox + (x - 1) * tile_size + mu, oy + (y - 1) * tile_size + mu, tile_size - 2 * mu, tile_size - 2 * mu)
		end

	center_text (message: READABLE_STRING_GENERAL; color: EV_COLOR)
			-- render a text message at the center of the screen
		do
			pixmap.set_foreground_color (color)
			pixmap.draw_text (width // 2, height // 2, message)
		end

end
