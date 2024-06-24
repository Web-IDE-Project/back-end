import { Box, Flex, IconButton } from '@chakra-ui/react'
import Sidebar from './Sidebar'
import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import { HamburgerIcon } from '@chakra-ui/icons'

const ContainerPage = () => {
  const [isSidebarOpen, setSidebarOpen] = useState(true)

  const toggleSidebar = () => {
    setSidebarOpen(prev => !prev)
  }

  return (
    <Flex h="full">
      {/* TODO - 왼쪽 사이드 바 애니메이션 구현 */}
      <Box
        w="250px"
        // transform={`translateX(${isSidebarOpen ? '0' : '-250px'})`}
        // transition="transform 0.3s ease"
        display={isSidebarOpen ? 'block' : 'none'}
      >
        <Sidebar toggleSidebar={toggleSidebar} />
      </Box>
      <Box flexGrow={1} bg="gray.50" overflow="scroll">
        {!isSidebarOpen && (
          <IconButton
            aria-label="open sidebar"
            icon={<HamburgerIcon />}
            onClick={toggleSidebar}
            position="absolute"
            top="1rem"
            left="1rem"
            zIndex="1"
          />
        )}
        <Outlet />
      </Box>
    </Flex>
  )
}

export default ContainerPage
