import { combineReducers, configureStore } from '@reduxjs/toolkit'
import ideReducer from './ideSlice'
import userReducer from './userSlice'
import {
  persistReducer,
  persistStore,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const rootReducer = combineReducers({
  ide: ideReducer,
  user: userReducer,
})

const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['user'], // userReducer만 persist 적용
}

const persistedReducer = persistReducer(persistConfig, rootReducer)

const store = configureStore({
  reducer: persistedReducer,
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
})

const persistor = persistStore(store)

export type RootState = ReturnType<typeof store.getState> // store의 state를 나타내는 타입
export type AppDispatch = typeof store.dispatch // 액션을 dispatch하는 함수의 타입

export { store, persistor }
